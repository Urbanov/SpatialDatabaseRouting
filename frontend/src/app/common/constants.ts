import {HttpHeaders} from "@angular/common/http";

export class Constants {
  static readonly HTTP_HEADERS = new HttpHeaders().set('Content-Type', 'application/json; charset=utf-8');
  static readonly API_PATH = 'api/find-route';
}
