import {Component, OnInit} from '@angular/core';
import {Flat} from "../domain/flat";
import {FlatService} from "../service/flat.service";
import {FilterParam} from "../domain/filter-param";
import {SortParam} from "../domain/sort-param";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {
  flats: Flat[];

  sortParams: SortParam[] = [];
  filterParams: FilterParam[] = [];

  constructor(private flatService: FlatService) {
  }

  ngOnInit(): void {
    this.reloadFlats();
  }

  reloadFlats() {
    this.flatService.findFlats(null, this.sortParams, this.filterParams).subscribe(res => {
      this.flats = res.content;
    });
  }

  sortParamsUpdated(sortParams: SortParam[]) {
    this.sortParams = sortParams;
    this.reloadFlats();
  }

  filterParamsUpdated(filterParams: FilterParam[]) {
    this.filterParams = filterParams;
    this.reloadFlats();
  }

}
