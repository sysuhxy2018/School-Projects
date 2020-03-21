import { Component, OnInit } from '@angular/core';
import { ResultsService} from '../results.service';
import { SearchService } from '../search.service';

@Component({
  selector: 'app-currently',
  templateUrl: './currently.component.html',
  styleUrls: ['./currently.component.css']
})
export class CurrentlyComponent implements OnInit {

  private data: Object = null;
  private city = "";
  private state = "";
  private star = "";
  private sealLink = "";
  constructor(private resultsService: ResultsService, private searchService: SearchService) {
    this.data = resultsService.getCurrentJson();
    this.city = searchService.getCity();
    this.state = searchService.getState();
    this.sealLink = this.searchService.retrieveImgLink();
    if (localStorage.getItem(this.city) == null) {
      this.star = 'star_border';
    }
    else {
      this.star = 'star';
    }
  }

  ngOnInit() {
  }

  addToFavour(): void {
    if (localStorage.getItem(this.city) == null) {
      localStorage.setItem(this.city, JSON.stringify({
        city: this.city,
        state: this.state,
        src: this.sealLink
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
}
