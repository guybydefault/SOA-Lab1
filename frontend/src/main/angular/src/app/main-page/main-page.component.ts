import {Component, OnInit, ViewChild} from '@angular/core';
import {Flat} from "../domain/flat";
import {FlatService} from "../service/flat.service";
import {FilterParam} from "../domain/filter-param";
import {SortParam} from "../domain/sort-param";
import {PageRequest} from "../domain/page-request";
import {FlatFormComponent} from "../flat-form/flat-form.component";
import {PageableDto} from "../domain/pageInfo";
import {ToastService} from "../service/toast.service";

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
  pageRequest: PageRequest = new PageRequest(0, 20)

  constructor(private flatService: FlatService, private toastService: ToastService) {
  }

  ngOnInit(): void {
    this.reloadFlats();
  }

  reloadFlats() {
    this.flatService.findFlats(this.pageRequest, this.sortParams, this.filterParams).subscribe(res => {
      this.flats = res.content;
      this.page = res;
      this.toastService.showSuccess(`Flats has been successfully loaded!`)
    }, error => {
      this.toastService.showError(`Server returned error (${error.status}: ${error.statusText})`);
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

  flatDeleted(flat: Flat) {
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
