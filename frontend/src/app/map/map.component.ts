import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {Road} from "../domain/road";
import {proj} from 'openlayers';
import {Point} from "../domain/point";

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit {

  public roads: Road[];
  public source: Point;
  public target: Point;

  @Output()
  clear: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  handleClick(event: any) {
    let coords: any[] = proj.transform(event.coordinate, 'EPSG:3857', 'EPSG:4326');

    if (!this.source || this.target) {
      this.clearMarkersAndRoute();
      this.source = Point.fromArray(coords);
    }
    else {
      this.target = Point.fromArray(coords);
    }
  }

  clearMarkersAndRoute() {
    this.roads = null;
    this.target = null;
    this.source = null;
    this.clear.emit();
  }
}
