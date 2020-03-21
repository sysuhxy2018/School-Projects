import { Component, OnInit } from '@angular/core';
import { SearchService } from '../search.service';
import { ResultsService} from '../results.service';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';

@Component({
  selector: 'app-results',
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.css']
})
export class ResultsComponent implements OnInit {

  status = "";
  currentTab = {data:null};
  hourTab = {data:[]};
  weekTab = {data:[]};
  city = "";
  complete = false;
  constructor(private searchService: SearchService, private resultsService: ResultsService, private router: Router) { 
    searchService.weatherData$.subscribe(list => {
      this.city = searchService.getCity();
      this.currentTab.data = {
        city: this.city,
        timezone: list['timezone'],
        temperature: Math.round(list['currently']['temperature']),
        summary: list['currently']['summary'],
        humidity: list['currently']['humidity'],
        pressure: list['currently']['pressure'],
        windSpeed: list['currently']['windSpeed'],
        visibility: list['currently']['visibility'],
        cloudCover: list['currently']['cloudCover'],
        ozone: list['currently']['ozone']
      };
      
      for (let i = 0; i < 24; i++) {
        this.hourTab.data[i] = {
          temperature: Math.round(list['hourly']['data'][i]['temperature']),
          pressure: list['hourly']['data'][i]['pressure'],
          humidity: list['hourly']['data'][i]['humidity'],
          ozone: list['hourly']['data'][i]['ozone'],
          visibility: list['hourly']['data'][i]['visibility'],
          windSpeed: list['hourly']['data'][i]['windSpeed']
        };
      }
      
      for (let i = 0; i < list['daily']['data'].length; i++) {
        this.weekTab.data[i] = {
          offset: list['offset'],
          time: list['daily']['data'][i]['time'],
          temperatureLow: Math.round(list['daily']['data'][i]['temperatureLow']),
          temperatureHigh: Math.round(list['daily']['data'][i]['temperatureHigh'])
        };
      }
      searchService.setPos(list['latitude'], list['longitude']);
      searchService.setTemperature(Math.round(list['currently']['temperature']) + "");
      searchService.setSummary(list['currently']['summary']);
      resultsService.setCurrentJson(this.currentTab);
      resultsService.setHourJson(this.hourTab);
      resultsService.setWeekJson(this.weekTab);
      this.router.navigate(['/currently']);
    })

    searchService.status$.subscribe(list => {
      this.status = list;
      if (this.status == "Success")
        this.complete = true;
    })

  }

  getCurrenData(obj: Object): void {

  }

  ngOnInit() {
  }

}
