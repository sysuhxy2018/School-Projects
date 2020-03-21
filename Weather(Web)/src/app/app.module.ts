import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import {MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatTooltipModule} from '@angular/material/tooltip';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SearchComponent } from './search/search.component';
import { ResultsComponent } from './results/results.component';
import { CurrentlyComponent } from './currently/currently.component';
import { HourlyComponent } from './hourly/hourly.component';
import { WeeklyComponent } from './weekly/weekly.component';
import {DialogOverviewExampleDialog} from './weekly/weekly.component';

import { ChartsModule } from 'ng2-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FavourComponent } from './favour/favour.component';
import { ErrorComponent } from './error/error.component';
import { BlankComponent } from './blank/blank.component';
@NgModule({
  declarations: [
    AppComponent,
    SearchComponent,
    ResultsComponent,
    CurrentlyComponent,
    HourlyComponent,
    WeeklyComponent,
    DialogOverviewExampleDialog,
    FavourComponent,
    ErrorComponent,
    BlankComponent
  ],
  entryComponents: [DialogOverviewExampleDialog],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ChartsModule,
    BrowserAnimationsModule,
    MatDialogModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatTooltipModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
