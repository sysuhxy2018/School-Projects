import { Component, OnInit, Inject} from '@angular/core';
import { ResultsService} from '../results.service';
import {SearchService} from '../search.service';
import * as CanvasJS from '../../assets/canvasjs.min';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'app-weekly',
  templateUrl: './weekly.component.html',
  styleUrls: ['./weekly.component.css']
})
export class WeeklyComponent implements OnInit {

  data: Object = {data:[]};

  private city = "";
  private state = "";
  private star = "";
  constructor(private dialog: MatDialog, private resultsService: ResultsService, private searchService: SearchService) {
    this.data = resultsService.getWeekJson();
    this.city = searchService.getCity();
    this.state = searchService.getState();
    if (localStorage.getItem(this.city) == null) {
      this.star = 'star_border';
    }
    else {
      this.star = 'star';
    }
  }

  addToFavour(): void {
    if (localStorage.getItem(this.city) == null) {
      localStorage.setItem(this.city, JSON.stringify({
        city: this.city,
        state: this.state,
        src: this.searchService.retrieveImgLink()
      }));
      this.star='star';
    }
    else {
      localStorage.removeItem(this.city);
      this.star ='star_border';
    }
  }

  newTweet(): void {
    let url = 'https://twitter.com/intent/tweet?';
    let query = 'text=The current temperature at ' + this.city + ' is ' + 
    this.searchService.getTemperature() + '℉. The weather conditions are ' +
    this.searchService.getSummary() + '.&hashtags=CSCI571WeatherSearch.';
    url += query;
    url = encodeURI(url);
    console.log(url);
    window.open(url, '__blank');
  }
  
  openDialog(): void {
    this.dialog.open(DialogOverviewExampleDialog);
  }

  getSummaryIcon(sum: string): string {
    let res:string = "";
    switch (sum) {
        case "clear-day":
        case "clear-night": res = "https://cdn3.iconfinder.com/data/icons/weather-344/142/sun-512.png"; break;
        case "rain": res = "https://cdn3.iconfinder.com/data/icons/weather-344/142/rain-512.png"; break;
        case "snow": res = "https://cdn3.iconfinder.com/data/icons/weather-344/142/snow-512.png"; break;
        case "sleet": res = "https://cdn3.iconfinder.com/data/icons/weather-344/142/lightning-512.png"; break;
        case "wind": res = "https://cdn4.iconfinder.com/data/icons/the-weather-is-nicetoday/64/weather_10-512.png"; break;
        case "fog": res = "https://cdn3.iconfinder.com/data/icons/weather-344/142/cloudy-512.png";
        case "cloudy": res = "https://cdn3.iconfinder.com/data/icons/weather-344/142/cloud-512.png"; break;
        case "partly-cloudy-day": 
        case "partly-cloudy-night": res = "https://cdn3.iconfinder.com/data/icons/weather-344/142/sunny-512.png"; break;
    }
    return res;
  }

  ngOnInit() {
    let rangeData = [];
    for (let i = 0; i < this.data['data'].length; i++) {
      // With offset, we can convert the GMT timestamp to local time. The display format is according to GMT timezone.
      let time = new Date(parseInt(this.data['data'][i]['time']) * 1000 + parseInt(this.data['data'][i]['offset']) * 3600 * 1000);
      let date = time.getUTCDate() + '/' + (time.getUTCMonth() + 1) + '/' + time.getUTCFullYear();
      
      rangeData[i] = {
        x: 10 * (this.data['data'].length - i),
        y: [Math.round(this.data['data'][i]['temperatureLow']), Math.round(this.data['data'][i]['temperatureHigh'])],
        label: date,
        click: (e) => {
          this.searchService.getDetails(this.data['data'][i]['time']).subscribe(list => {
            this.dialog.open(DialogOverviewExampleDialog, {
              width: '450px',
              position: {
                top: '30px'
              },
              data: {
                date: date,
                city: this.searchService.getCity(),
                temperature: list['currently']['temperature'],
                summary: list['currently']['summary'],
                icon: this.getSummaryIcon(list['currently']['icon']),
                precipitation: Math.round(list['currently']['precipIntensity'] * 100) / 100,
                chance: Math.round(list['currently']['precipProbability'] * 100),
                windSpeed: Math.round(list['currently']['windSpeed'] * 100) / 100,
                humidity: list['currently']['humidity'] * 100,
                visibility: list['currently']['visibility']
              },
              panelClass: 'custom-dialog-container'
            });
          })
        }
      }
    };
    let chart = new CanvasJS.Chart("chartContainer", {
      animationEnabled: true,
      exportEnabled: false,
      title: {
        text: "Weekly Weather"
      },
      legend: {
        horizontalAlign: "center", // left, center ,right 
        verticalAlign: "top",  // top, center, bottom
      },
      axisX: {
        title: "Days"
      },
      axisY: {
        includeZero: false,
        title: "Temperature in Fahrenheit",
        interval: 10,
        gridThickness: 0,
        stripLines: [
          {
            value: 0,
            showOnTop: true,
            color: "gray",
            thickness: 2
          }
        ]
      },
      data: [{
        type: "rangeBar",
        indexLabel: "{y[#index]}",
        showInLegend: true,
        legendText: "Day wise temperature range",
        toolTipContent: "<b>{label}: {y[0]} to {y[1]}</b>",
        color: "#9ad1f1",
        dataPoints: rangeData
      }]
    });
      
    chart.render();
  }

}

@Component({
  selector: 'dialog-overview-example-dialog',
  templateUrl: 'dialog-overview-example-dialog.html',
  styleUrls: ['weekly.dialog.css']
})
export class DialogOverviewExampleDialog {

  constructor(
    public dialogRef: MatDialogRef<DialogOverviewExampleDialog>, @Inject(MAT_DIALOG_DATA) public data: any) {}

}
