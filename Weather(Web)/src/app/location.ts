export class Location {
    
    constructor (
      public street: string,
      public city: string,
      public state: string,
      public lat: number,
      public lon: number,
      public flag: boolean) {
    }
  }