import { Component, OnInit } from '@angular/core';
import {Location} from '../location';
import { SearchService } from '../search.service';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import { resetFakeAsyncZone } from '@angular/core/testing';
import { Observable } from 'rxjs';
import { ResultsComponent } from '../results/results.component';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

  stateList = {
    "States":[
   {
   "Abbreviation":"Select State",
   "State":"Select State"
   },
   {
   "Abbreviation":"AL",
   "State":"Alabama"
   },
   {
   "Abbreviation":"AK",
   "State":"Alaska"
   },
   {
   "Abbreviation":"AZ",
   "State":"Arizona"
   },
   {
   "Abbreviation":"AR",
   "State":"Arkansas"
   },
   {
   "Abbreviation":"CA",
   "State":"California"
   },
   {
   "Abbreviation":"CO",
   "State":"Colorado"
   },
   {
   "Abbreviation":"CT",
   "State":"Connecticut"
   },
   {
   "Abbreviation":"DE",
   "State":"Delaware"
   },
   {
   "Abbreviation":"DC",
   "State":"District Of Columbia"
   },
   {
   "Abbreviation":"FL",
   "State":"Florida"
   },
   {
   "Abbreviation":"GA",
   "State":"Georgia"
   },
   {
   "Abbreviation":"HI",
   "State":"Hawaii"
   },
   {
   "Abbreviation":"ID",
   "State":"Idaho"
   },
   {
   "Abbreviation":"IL",
   "State":"Illinois"
   },
   {
   "Abbreviation":"IN",
   "State":"Indiana"
   },
   {
   "Abbreviation":"IA",
   "State":"Iowa"
   },
   {
   "Abbreviation":"KS",
   "State":"Kansas"
   },
   {
   "Abbreviation":"KY",
   "State":"Kentucky"
   },
   {
   "Abbreviation":"LA",
   "State":"Louisiana"
   },
   {
   "Abbreviation":"ME",
   "State":"Maine"
   },
   {
   "Abbreviation":"MD",
   "State":"Maryland"
   },
   {
   "Abbreviation":"MA",
   "State":"Massachusetts"
   },
   {
   "Abbreviation":"MI",
   "State":"Michigan"
   },
   {
   "Abbreviation":"MN",
   "State":"Minnesota"
   },{
   "Abbreviation":"MS",
   "State":"Mississippi"
   },{
   "Abbreviation":"MO",
   "State":"Missouri"
   },{
   "Abbreviation":"MT",
   "State":"Montana"
   },{
   "Abbreviation":"NE",
   "State":"Nebraska"
   },{
   "Abbreviation":"NV",
   "State":"Nevada"
   },{
   "Abbreviation":"NH",
   "State":"New Hampshire"
   },{
   "Abbreviation":"NJ",
   "State":"New Jersey"
   },{
   "Abbreviation":"NM",
   "State":"New Mexico"
   },{
   "Abbreviation":"NY",
   "State":"New York"
   },{
   "Abbreviation":"NC",
   "State":"North Carolina"
   },{
   "Abbreviation":"ND",
   "State":"North Dakota"
   },{
   "Abbreviation":"OH",
   "State":"Ohio"
   },{
   "Abbreviation":"OK",
   "State":"Oklahoma"
   },{
   "Abbreviation":"OR",
   "State":"Oregon"
   },{
   "Abbreviation":"PA",
   "State":"Pennsylvania"
   },{
   "Abbreviation":"RI",
   "State":"Rhode Island"
   },{
   "Abbreviation":"SC",
   "State":"South Carolina"
   },{
   "Abbreviation":"SD",
   "State":"South Dakota"
   },{
   "Abbreviation":"TN",
   "State":"Tennessee"
   },{
   "Abbreviation":"TX",
   "State":"Texas"
   },{
   "Abbreviation":"UT",
   "State":"Utah"
   },{
   "Abbreviation":"VT",
   "State":"Vermont"
   },{
   "Abbreviation":"VA",
   "State":"Virginia"
   },{
   "Abbreviation":"WA",
   "State":"Washington"
   },{
   "Abbreviation":"WV",
   "State":"West Virginia"
   },{
   "Abbreviation":"WI",
   "State":"Wisconsin"
   },
   {
   "Abbreviation":"WY",
   "State":"Wyoming"
   }
   ]
   };

  addr = '/blank';
  isChecked = false;
  disableAll = false;
  location = new Location(null, null, this.stateList.States[0].Abbreviation, 0, 0, false);

  constructor(private searchService: SearchService, 
    private router: Router) {
      this.searchService.page$.subscribe(list=>{
        this.addr = list;
      })
     }

  public resetInputs(searchForm) {
    searchForm.form.controls['street'].reset();
    searchForm.form.controls['city'].reset();
    this.location.street = "";
    this.location.city = "";
    this.location.state = "Select State";
    this.autoOptions.length = 0;
    if (this.isChecked) {
      this.disableAll = true;
    }
    else {
      this.disableAll = false;
    }
  }

  public clearAll(searchForm) {
    searchForm.form.controls['street'].reset();
    searchForm.form.controls['city'].reset();
    this.autoOptions.length = 0;
    this.isChecked = false;
    this.disableAll = false;
    this.location.street = "";
    this.location.city = "";
    this.location.state = "Select State";

    // clearResults
    this.addr = '/blank';
    this.redirect('/blank');
  }

  autoOptions = [];
  updateOptions() {
    this.searchService.getPredictions(this.location.city).subscribe(list=>{
      let candidates:Array<string> = [];

      // For zero result, list['predictions'] will be an empty array
      let arr = list['predictions'];
      for (let i = 0; i < arr.length; i++) {
        candidates.push(arr[i]['structured_formatting']['main_text']);
      }
      this.autoOptions = candidates;
    });
  }

  getPos(): void {
    this.searchService.getPos().subscribe(list => {
      this.location.lat = list["lat"];
      this.location.lon = list["lon"];
      this.location.city = list["city"];
      this.location.state = list['region'];
      this.location.flag = false;
      this.getData();
    });
  }

  getData(): void {
    this.searchService.getImgLink(this.location.state + ' State ' + 'Seal').subscribe(list => {
      this.searchService.setImgLink(list['items'][0]['link']);
      this.searchService.getData(this.location).subscribe(
        list => {
          this.searchService.setStatus('Success');
          if (list.hasOwnProperty('status') && list['status'] == "ZERO_RESULTS") {
            this.addr = '/error';
            this.redirect('/error');
          }
          this.searchService.setSwapData(list);
        }
      );
    });
  }

  onSubmit(): void {
    this.searchService.setStatus('Loading');
    if (this.isChecked) {
      this.getPos();
    }
    else {
      this.location.lat = 0;
      this.location.lon = 0;
      this.location.flag = true;
      this.getData();
    }
    this.addr = "/blank";
    this.redirect('/results');
  }

  redirect(addr: string): void {
    if (this.addr == "/blank" && addr == "/results") {
      this.addr = addr;
    }
    else if (this.addr == "/results" && addr == "/results") {
      this.router.navigate(['/currently']);
      return;
    }
    this.router.navigate([addr]);
  }

  ngOnInit() {
  }

  // TODO: Remove this when we're done
  // get diagnostic() { return JSON.stringify(this.location); }
}
