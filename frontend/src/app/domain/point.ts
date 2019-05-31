export class Point {
  latitude: string;
  longitude: string;

  static fromArray(array: any[]) {
    return <Point> {
      latitude: array[1],
      longitude: array[0]
    }
  }
}
