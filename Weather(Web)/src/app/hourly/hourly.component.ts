import { Component, OnInit, ViewChild} from '@angular/core';
import { ResultsService} from '../results.service';
import { ChartOptions, ChartType, ChartDataSets } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { SearchService } from '../search.service';
// import { runInThisContext } from 'vm';

@Component({
  selector: 'app-hourly',
  templateUrl: './hourly.component.html',
  styleUrls: ['./hourly.component.css']
})
export class HourlyComponent implements OnInit {
  
  private city = "";
  private state = "";
  private star = "";
  private data: Object = null;
  private titleOptions = [
    {
      title: 'temperature',
      unit: 'Fahrenheit'
    },
    {
      title: 'pressure',
      unit: 'Millibars'
    },
    {
      title: 'humidity',
      unit: '%'
    },
    {
      title: 'ozone',
      unit: 'Dobson Units'
    },
    {
      title: 'visibility',
      unit: 'Miles'
    },
    {
      title: 'windSpeed',
      unit: 'Miles per hour'
    }
  ];
  dataOptions = {
    name: "",
    datas: [],
    unit: ""
  };
  constructor(private resultsService: ResultsService, private searchService: SearchService) {
    this.data = resultsService.getHourJson();
    this.city = searchService.getCity();
    this.state = searchService.getState();
    this.setChartData('temperature');
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
    window.open(url, '__blank');
  }
  
  private setChartData(name: string): void {
    for (let i = 0; i < this.titleOptions.length; i++) {
      if (this.titleOptions[i].title == name) {
        this.dataOptions.name = name;
        this.dataOptions.unit = this.titleOptions[i].unit;
        for (let i = 0; i < this.data["data"].length; i++) {
          this.dataOptions.datas[i] = this.data["data"][i][this.dataOptions.name];
        }
        break;
      }
    }
    this.updateChart();
  }

  private updateChart(): void {
    let ma = Math.max.apply(null,this.dataOptions.datas);
    let mi = Math.min.apply(null,this.dataOptions.datas);
    let step = (ma - mi) / 23;
    this.barChartOptions = {
    
      legend: {
        onClick:(e,legend)=>{}
      },
      responsive: true,
      scales: {
        yAxes: [{
          scaleLabel: {
            display: true,
            labelString: this.dataOptions.unit
          },
          ticks: {
            suggestedMax: ma + step * 2,
            suggestedMin: mi - step * 2
          }
        }],
        xAxes: [{
          scaleLabel: {
            display: true,
            labelString: 'Time difference from current hour'
          }
        }]
    }
    };
    this.barChartData = [
      {data: this.dataOptions.datas, 
        label: this.dataOptions.name
      }
    ];
  }

  dataChange(): void {
    this.setChartData(this.dataOptions.name);
  }

  public barChartOptions: ChartOptions = {
    
    responsive: true,
    scales: {
      yAxes: [{
        scaleLabel: {
          display: true,
          labelString: ""
        }
      }],
      xAxes: [{
        scaleLabel: {
          display: true,
          labelString: 'Time difference from current hour'
        }
      }]
  }
  };
  public barChartLabels = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12',
'13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23'];
  public barChartType: ChartType = 'bar';
  public barChartLegend = true;
  public barChartColor = [{backgroundColor: 'rgba(154,209,241,1.0)'}];
  public barChartData: ChartDataSets[]= [
    {data: [], 
      label: ""
    }
  ];
  
  ngOnInit() {
  }

}
