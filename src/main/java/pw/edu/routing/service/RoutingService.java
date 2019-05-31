package pw.edu.routing.service;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.edu.routing.domain.PointEntity;
import pw.edu.routing.domain.RoadEntity;
import pw.edu.routing.repository.RoutingRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class RoutingService {

    private final RoutingRepository routingRepository;

    private final WKTReader wktReader;

    @Autowired
    public RoutingService(RoutingRepository routingRepository, WKTReader wktReader) {
        this.routingRepository = routingRepository;
        this.wktReader = wktReader;
    }

    public List<RoadEntity> findRouteBetween(PointEntity source, PointEntity target, Double lengthThreshold) {
        Integer sourceId = routingRepository.findNodeClosestToPoint(convertPoint(source));
        Integer targetId = routingRepository.findNodeClosestToPoint(convertPoint(target));
        List<RoadEntity> shortestRoute = routingRepository.findShortestPathBetweenNodes(sourceId, targetId);

        while (getSpecialFragmentsLength(shortestRoute) < lengthThreshold) {
            List<Integer> specialFragments = shortestRoute.stream()
                .filter(RoadEntity::getSpecial)
                .map(RoadEntity::getId)
                .collect(Collectors.toList());
            List<RoadEntity> toRemove = findLongestNonSpecialRoute(shortestRoute);
            List<RoadEntity> toAdd = prepareRouteContainingSpecialFragment(toRemove, specialFragments);
            replaceFragment(shortestRoute, toRemove, toAdd);
        }

        return shortestRoute;
    }

    @SneakyThrows
    private Point convertPoint(PointEntity pointEntity) {
        Geometry geometry = wktReader.read(String.format("POINT (%s %s)", pointEntity.getLongitude(), pointEntity.getLatitude()));
        return geometry.getInteriorPoint();
    }

    private List<RoadEntity> findLongestNonSpecialRoute(List<RoadEntity> route) {
        int[] indexList = Stream.of(
                IntStream.of(-1),
                IntStream.range(0, route.size()).filter(i -> route.get(i).getSpecial()),
                IntStream.of(route.size())
            )
            .flatMapToInt(e -> e)
            .toArray();

        return IntStream.range(0, indexList.length - 1)
            .mapToObj(i -> route.subList(indexList[i] + 1, indexList[i + 1]))
            .max(Comparator.comparingDouble(this::getRouteLength))
            .orElseGet(Collections::emptyList);
    }

    private Double getRouteLength(List<RoadEntity> route) {
        return route.stream()
            .mapToDouble(RoadEntity::getLength)
            .sum();
    }

    private Double getSpecialFragmentsLength(List<RoadEntity> route) {
        return route.stream()
            .filter(RoadEntity::getSpecial)
            .mapToDouble(RoadEntity::getLength)
            .sum();
    }

    private List<RoadEntity> prepareRouteContainingSpecialFragment(List<RoadEntity> route, List<Integer> usedFragments) {
        Integer source = route.get(0).getTarget().equals(route.get(1).getSource()) || route.get(0).getTarget().equals(route.get(1).getTarget())
            ? route.get(0).getSource()
            : route.get(0).getTarget();

        Integer target = route.get(route.size() - 1).getSource().equals(route.get(route.size() - 2).getSource()) || route.get(route.size() - 1).getSource().equals(route.get(route.size() - 2).getTarget())
            ? route.get(route.size() - 1).getTarget()
            : route.get(route.size() - 1).getSource();

        Integer specialFragment = routingRepository.findLongestSpecialEdgeClosestToNodesAndNotIn(source, target, usedFragments);
        return routingRepository.findShortestPathBetweenNodesThroughEdge(source, target, specialFragment);
    }

    private void replaceFragment(List<RoadEntity> route, List<RoadEntity> toRemove, List<RoadEntity> toAdd) {
        int index = IntStream.range(0, route.size())
            .boxed()
            .filter(i -> route.get(i).equals(toRemove.get(0)))
            .filter(offset -> IntStream.range(0, toRemove.size()).allMatch(i -> toRemove.get(i).equals(route.get(i + offset))))
            .findAny()
            .orElse(0);

        route.subList(index, toRemove.size() + index).clear();
        route.addAll(toAdd);
    }
}
