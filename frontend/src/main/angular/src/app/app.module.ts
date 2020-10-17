import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {HttpClientModule} from "@angular/common/http";
import { FlatTableComponent } from './flat-table/flat-table.component';
import { MainPageComponent } from './main-page/main-page.component';
import { SortSettingsComponent } from './sort-settings/sort-settings.component';
import { FilterSettingsComponent } from './filter-settings/filter-settings.component';

@NgModule({
  declarations: [
    AppComponent,
    FlatTableComponent,
    MainPageComponent,
    SortSettingsComponent,
    FilterSettingsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
