package pw.edu.routing.repository;

import com.vividsolutions.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pw.edu.routing.domain.RoadEntity;

import java.util.List;

public interface RoutingRepository extends JpaRepository<RoadEntity, Integer> {

    @Query(nativeQuery = true, value =
        "SELECT id FROM roads_vertices_pgr " +
        "ORDER BY ST_Distance(the_geom, ST_SetSRID(CAST(:point AS geometry), 4326), TRUE) " +
        "ASC " +
        "LIMIT 1")
    Integer findNodeClosestToPoint(@Param("point") Point point);

    @Query(nativeQuery = true, value =
        "SELECT id, geom, special, cost AS length, source, target " +
        "FROM pgr_dijkstra('SELECT id, source, target, st_length(geom, true) as cost FROM roads', :firstNodeId, :secondNodeId, FALSE) AS path " +
        "JOIN roads ON path.edge = roads.id")
    List<RoadEntity> findShortestPathBetweenNodes(@Param("firstNodeId") Integer firstNodeId, @Param("secondNodeId") Integer secondNodeId);

    @Query(nativeQuery = true, value =
        "SELECT closest.id FROM" +
        "   (SELECT id, ST_Distance(geom, (SELECT the_geom FROM roads_vertices_pgr WHERE id = :firstNodeId)) + ST_Distance(geom, (SELECT the_geom FROM roads_vertices_pgr WHERE id = :secondNodeId)), ST_Length(geom, TRUE) FROM roads" +
        "   WHERE special = TRUE AND id NOT IN :edges AND NOT (source = :firstNodeId AND target = :secondNodeId OR source = :secondNodeId AND target = :firstNodeId)" +
        "   ORDER BY (ST_Distance(geom, (SELECT the_geom FROM roads_vertices_pgr WHERE id = :firstNodeId)) + ST_Distance(geom, (SELECT the_geom FROM roads_vertices_pgr WHERE id = :secondNodeId))) ASC" +
        "   LIMIT 10) AS closest " +
        "ORDER BY closest.st_length DESC " +
        "LIMIT 1")
    Integer findLongestSpecialEdgeClosestToNodesAndNotIn(@Param("firstNodeId") Integer firstNodeId, @Param("secondNodeId") Integer secondNodeId, @Param("edges") List<Integer> edges);

    @Query(nativeQuery = true, value =
        "SELECT id, geom, special, cost AS length, source, target " +
        "FROM pgr_dijkstraVia(" +
        "   'SELECT id, source, target, st_length(geom, true) as cost FROM roads'," +
        "    ARRAY[:firstNodeId, (SELECT source FROM roads WHERE id = :edgeId), (SELECT target FROM roads WHERE id = :edgeId), :secondNodeId]," +
        "    FALSE, FALSE, TRUE) AS path " +
        "JOIN roads ON path.edge = roads.id")
    List<RoadEntity> findShortestPathBetweenNodesThroughEdge(@Param("firstNodeId") Integer firstNodeId, @Param("secondNodeId") Integer secondNodeId, @Param("edgeId") Integer edgeId);
}
