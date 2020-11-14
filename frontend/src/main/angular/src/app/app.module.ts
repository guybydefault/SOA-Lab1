import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {HttpClientModule} from "@angular/common/http";
import {FlatTableComponent} from './flat-table/flat-table.component';
import {MainPageComponent} from './main-page/main-page.component';
import {SortSettingsComponent} from './sort-settings/sort-settings.component';
import {FilterSettingsComponent} from './filter-settings/filter-settings.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {FlatFormComponent} from './flat-form/flat-form.component';
import { PageRequestComponent } from './page-request/page-request.component';
import { ToastComponent } from './toast/toast.component';

@NgModule({
  declarations: [
    AppComponent,
    FlatTableComponent,
    MainPageComponent,
    SortSettingsComponent,
    FilterSettingsComponent,
    FlatFormComponent,
    PageRequestComponent,
    ToastComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
