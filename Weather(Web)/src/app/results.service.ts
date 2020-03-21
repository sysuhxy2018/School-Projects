import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ResultsService {

  private currentJson: Object = null;
  private hourJson: Object = null;
  private weekJson: Object = null;
  constructor() { }

  setCurrentJson(obj: Object): void {
    this.currentJson = obj;
  }

  getCurrentJson(): Object {
    return this.currentJson;
  }

  setHourJson(obj: Object): void {
    this.hourJson = obj;
  }

  getHourJson(): Object {
    return this.hourJson;
  }

  setWeekJson(obj: Object): void {
    this.weekJson = obj;
  }

  getWeekJson(): Object {
    return this.weekJson;
  }
}
