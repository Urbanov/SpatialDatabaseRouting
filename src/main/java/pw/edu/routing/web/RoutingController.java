package pw.edu.routing.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.edu.routing.domain.PointEntity;
import pw.edu.routing.service.RoutingService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoutingController {

    private final RoutingService routingService;

    @Autowired
    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @PostMapping("/find-route")
    public ResponseEntity findRoute(@RequestBody List<PointEntity> points, @RequestParam Double lengthThreshold) {
        return ResponseEntity.ok(routingService.findRouteBetween(points.get(0), points.get(1), lengthThreshold));
    }
}
