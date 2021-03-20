import {Component, OnInit, ViewChild} from '@angular/core';
import {Flat} from "../domain/flat";
import {FlatService} from "../service/flat.service";
import {FilterParam} from "../domain/filter-param";
import {SortParam} from "../domain/sort-param";
import {PageRequest} from "../domain/page-request";
import {FlatFormComponent} from "../flat-form/flat-form.component";
import {PageableDto} from "../domain/pageInfo";
import {ToastService} from "../service/toast.service";
import {SortDistanceParam} from "../domain/sort-distance-param";

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {
  flats: Flat[];
  page: PageableDto<Flat>;

  @ViewChild('flatForm')
  flatForm: FlatFormComponent;

  sortParams: SortParam[] = [];
  filterParams: FilterParam[] = [];
  sortDistanceParam: SortDistanceParam = undefined;
  pageRequest: PageRequest = new PageRequest(0, 20)

  constructor(private flatService: FlatService, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.reloadFlats();
  }

  reloadFlatsOrderedByDistance() {
    this.flatService.getOrderedByTimeToMetro(this.sortDistanceParam.wayOfTransport, this.sortDistanceParam.order).subscribe(res => {
      this.flats = res;
      this.toastService.showSuccess(`Flats has been successfully loaded!`)
    }, error => {
      this.toastService.showError(`Server returned error (${error.status}: ${error.statusText})`);
    });
  }

  showPagination() : boolean {
    return this.sortDistanceParam == undefined;
  }

  reloadFlats() {
    if (this.showPagination()) {
      this.reloadFlatsWithPagination();
    } else {
      this.reloadFlatsOrderedByDistance();
    }
  }

  reloadFlatsWithPagination() {
    this.flatService.findFlats(this.pageRequest, this.sortParams, this.filterParams).subscribe(res => {
      this.flats = res.content;
      this.page = res;
      this.toastService.showSuccess(`Flats has been successfully loaded!`)
    }, error => {
      this.toastService.showError(`Server returned error (${error.status}: ${error.statusText})`);
    });
  }

  private turnPaginationOn() {
    this.sortDistanceParam = undefined;
  }

  sortParamsUpdated(sortParams: SortParam[]) {
    this.sortParams = sortParams;
    this.turnPaginationOn();
    this.reloadFlats();
  }

  filterParamsUpdated(filterParams: FilterParam[]) {
    this.filterParams = filterParams;
    this.turnPaginationOn();
    this.reloadFlats();
  }

  distanceOrderUpdated(sortDistanceParam: SortDistanceParam) {
    this.sortDistanceParam = sortDistanceParam;
    this.reloadFlats();
  }

  flatDeleted(flat: Flat) {
    this.reloadFlats();
  }

  flatCompared(flat: Flat) {
    this.reloadFlats();
  }

  flatEdited(flat: Flat) {
    this.reloadFlats();
  }

  pageRequestUpdated(pageRequest: PageRequest) {
    this.pageRequest = pageRequest;
    this.reloadFlats();
  }
}
