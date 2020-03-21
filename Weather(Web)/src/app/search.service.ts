import { Injectable } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { MessageService } from './message.service';
import {Location} from './location';

@Injectable({
  providedIn: 'root'
})
export class SearchService {

  private ipUrl = 'http://ip-api.com/json';
  private dataUrl = 'http://homework8-258118.appspot.com/weatherSearch';
  private imgUrl = 'http://homework8-258118.appspot.com/imageSearch';
  private autoUrl = 'http://homework8-258118.appspot.com/autoSearch';

  // Only for local debug
  // private dataUrl = 'http://localhost:3000/weatherSearch';
  // private imgUrl = 'http://localhost:3000/imageSearch';
  // private autoUrl = 'http://localhost:3000/autoSearch';

  private weatherDataSource = new Subject<Object>();
  private statusSource = new Subject<string>();
  private pageSource = new Subject<string>();

  public city = "";
  private lat = "";
  private lon = "";
  private state = "";
  private temperature = "";
  private summary = "";
  private imgLink = "";

  weatherData$ = this.weatherDataSource.asObservable();
  status$ = this.statusSource.asObservable();
  page$ = this.pageSource.asObservable();

  constructor(private http: HttpClient, private messageService: MessageService) { }

  getPos(): Observable<Object> {
    return this.http.get<Object>(this.ipUrl);
  }

  getData(entry: Location): Observable<Object> {
    this.city = entry.city;
    this.lat = entry.lat + "";
    this.lon = entry.lon + "";
    this.state = entry.state;

    let options = { params: new HttpParams().set('street', entry.street).set('city', this.city).
    set('state', entry.state).set('lat', entry.lat + "").set('lon', entry.lon + "").set('flag', entry.flag + "")};
    return  this.http.get<Location>(this.dataUrl, options);
  }

  setSwapData(obj: Object) {
    this.weatherDataSource.next(obj);
  }

  setStatus(msg: string) {
    this.statusSource.next(msg);
  }

  setPage(site: string) {
    this.pageSource.next(site);
  }

  setPos(lat: string, lon: string) {
    this.lat = lat;
    this.lon = lon;
  }

  getCity(): string {
    return this.city;
  }

  getDetails(time: any): Observable<Object> {
    let options = {params: new HttpParams().set('lat', this.lat).set('lon', this.lon).set('time', time)};
    return this.http.get(this.dataUrl, options);
  }

  getState(): string {
    return this.state;
  }

  setTemperature(t: string) {
    this.temperature = t;
  }

  getTemperature(): string {
    return this.temperature;
  }

  setSummary(s: string) {
    this.summary = s;
  }

  getSummary(): string {
    return this.summary;
  }

  getImgLink(searchParam: string): Observable<Object>{
    let options = {params: new HttpParams().set('q', searchParam)};
    return this.http.get(this.imgUrl, options);
  }

  setImgLink(link:string): void {
    this.imgLink = link;
  }

  retrieveImgLink(): string {
    return this.imgLink;
  }

  getPredictions(txt: string): Observable<Object>{
    let options = {params: new HttpParams().set('input', txt)};
    return this.http.get(this.autoUrl, options);
  } 
}
