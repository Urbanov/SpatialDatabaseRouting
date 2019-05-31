import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {HttpService} from "./service/http-service";
import {Road} from "./domain/road";
import {MapComponent} from "./map/map.component";
import { BlockUI, NgBlockUI } from 'ng-block-ui';
import {MatSnackBar} from "@angular/material";
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  @ViewChild(MapComponent)
  private mapComponent: MapComponent;

  @BlockUI()
  blockUI: NgBlockUI;

  public lengthSum: number;

  public lengthThreshold: number;

  constructor(private httpService: HttpService, private snackBar: MatSnackBar) {}

  updateRoute() {
    this.blockUI.start();
    this.httpService.getRoute(this.mapComponent.source, this.mapComponent.target, this.lengthThreshold).subscribe(

      data => {
        this.blockUI.stop();
        this.mapComponent.roads = data.map(data => Road.fromServerData(data));
        this.setLengthSum(this.mapComponent.roads
          .filter(road => road.special)
          .map(road => road.length)
          .reduce((a, b) => a + b, 0));
      },

      error => {
        this.blockUI.stop();
        this.mapComponent.clearMarkersAndRoute();
        this.snackBar.open('Błąd: wyznaczanie trasy się nie powiodło', '', {duration: 3000});
      });
  }

  isDataFilled(): boolean {
    return this.mapComponent.source && this.mapComponent.target && this.lengthThreshold != null;
  }

  setLengthSum(value: number) {
    this.lengthSum = value;
  }
}
