import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { SearchService } from '../search.service';
import { ResultsService} from '../results.service';
import { Location } from '../location';

@Component({
  selector: 'app-favour',
  templateUrl: './favour.component.html',
  styleUrls: ['./favour.component.css']
})
export class FavourComponent implements OnInit {

  isZeroRecord = false;
  public data = new Array();
  constructor(private router: Router, private searchService: SearchService, private resultsService: ResultsService) {
    this.getLocalData();
  }

  getLocalData() {
    if (localStorage.length == 0) {
      this.isZeroRecord = true;
    }
    else {
      this.isZeroRecord = false;
    }
    for (let i = 0; i < localStorage.length; i++) {
      let str = localStorage.getItem(localStorage.key(i));
      let obj = JSON.parse(str);
      this.data.push({
        num: i + 1,
        info: obj
      });
    }
  }

  deleteFavour(city: string):void {
    localStorage.removeItem(city);
    this.data.length = 0;
    this.getLocalData();
  }

  renewAllData(city: string): void {
    let str = localStorage.getItem(city);
    let obj = JSON.parse(str);
    this.searchService.setStatus("Loading");
    this.searchService.setPage('/results');
    this.getData(new Location(null, obj.city, obj.state, 0 ,0, true));
    this.router.navigate(['/results']);
  }

  getData(entry: Location): void {
    this.searchService.getImgLink(entry.state + ' State ' + 'Seal').subscribe(list => {
      this.searchService.setImgLink(list['items'][0]['link']);
      this.searchService.getData(entry).subscribe(
        list => {
          this.searchService.setSwapData(list);
          this.searchService.setStatus('Success');
        }
      );
    });
  }

  ngOnInit() {
  }

}
