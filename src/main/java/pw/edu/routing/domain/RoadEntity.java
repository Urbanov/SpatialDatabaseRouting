package pw.edu.routing.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.LineString;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class RoadEntity {

    @Id
    @JsonIgnore
    private Integer id;

    @Column(name = "special")
    private Boolean special;

    @Column(name = "geom", columnDefinition = "geometry(LineString, 4326)")
    private LineString road;

    @Column(name = "length")
    private Double length;

    @Column(name = "source")
    @JsonIgnore
    private Integer source;

    @Column(name = "target")
    @JsonIgnore
    private Integer target;
}
