import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {PageRequest} from "../domain/page-request";
import {PageableDto} from "../domain/pageInfo";
import {Flat} from "../domain/flat";

@Component({
  selector: 'app-page-request',
  templateUrl: './page-request.component.html',
  styleUrls: ['./page-request.component.css']
})
export class PageRequestComponent implements OnInit {

  @Input() public page: PageableDto<Flat>

  @Output() public pageRequestUpdated = new EventEmitter<PageRequest>();

  pageSize = 20;

  constructor() {
  }

  ngOnInit(): void {
  }

  setPageSize(pageSize: number) {
    this.pageSize = pageSize;
    this.pageRequestUpdated.emit(new PageRequest(this.page.number, this.pageSize));
  }

  setPage(i: number) {
    if (i >= 0 && i < this.page.totalPages) {
      this.pageRequestUpdated.emit(new PageRequest(i, this.pageSize));
    }
  }
}
