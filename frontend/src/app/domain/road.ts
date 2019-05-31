export class Road {
  special: boolean;
  coordinates: any[];
  length: number;

  public static fromServerData(road: any): Road {
    return <Road> {
      special: road.special,
      coordinates: road.road.coordinates,
      length: road.length
    }
  }
}
