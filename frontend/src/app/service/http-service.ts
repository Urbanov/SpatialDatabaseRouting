import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Constants} from "../common/constants";
import {Observable} from "rxjs";
import {Point} from "../domain/point";

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  constructor(private http: HttpClient) {}

  getRoute(source: Point, target: Point, lengthThreshold: number): Observable<any[]> {
    return this.http.post<any[]>(
      Constants.API_PATH,
      [source, target],
      {params: {lengthThreshold : (lengthThreshold * 1000).toString()}, headers: Constants.HTTP_HEADERS}
      );
  }
}
