import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {CurrentlyComponent} from './currently/currently.component';
import {HourlyComponent} from './hourly/hourly.component';
import {WeeklyComponent} from './weekly/weekly.component';
import {ResultsComponent} from './results/results.component';
import {FavourComponent} from './favour/favour.component';
import {ErrorComponent} from './error/error.component';
import {BlankComponent} from './blank/blank.component';


const routes: Routes = [
  {path: 'currently', component: CurrentlyComponent},
  {path: 'hourly', component: HourlyComponent},
  {path: 'weekly', component: WeeklyComponent},
  {path: 'results', component: ResultsComponent},
  {path: 'favour', component: FavourComponent},
  {path: 'error', component: ErrorComponent},
  {path: 'blank', component: BlankComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
