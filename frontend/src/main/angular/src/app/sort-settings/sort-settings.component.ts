import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {SortParam} from "../domain/sort-param";
import {fields} from "../domain/flat";

@Component({
  selector: 'app-sort-settings',
  templateUrl: './sort-settings.component.html',
  styleUrls: ['./sort-settings.component.css']
})
export class SortSettingsComponent implements OnInit {

  @Output() sortUpdated = new EventEmitter<SortParam[]>();

  private sortParams: SortParam[] = [];

  fields = fields;

  constructor() {
  }

  ngOnInit(): void {
  }

}

