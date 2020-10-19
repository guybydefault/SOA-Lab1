import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Order, SortParam} from "../domain/sort-param";
import {Field, FLAT_FIELDS} from "../domain/flat";

@Component({
  selector: 'app-sort-settings',
  templateUrl: './sort-settings.component.html',
  styleUrls: ['./sort-settings.component.css']
})
export class SortSettingsComponent implements OnInit {

  @Output() sortUpdated = new EventEmitter<SortParam[]>();

  selectedSortParams: SortParam[] = [];

  Order = Order;

  fields: Field[];
  selectedField: Field;

  constructor() {
  }

  ngOnInit(): void {
    this.initFields()
  }

  addSortParam(order: Order) {
    if (this.validate()) {
      this.selectedSortParams.push(new SortParam(this.selectedField, order))
      let ind = this.fields.indexOf(this.selectedField);
      this.fields.splice(ind, 1);
      this.sortUpdated.emit(this.selectedSortParams)
    }
  }

  initFields() {
    this.fields = Object.assign([], FLAT_FIELDS);
  }

  validate(): boolean {
    //TODO
    return true;
  }

  clear() {
    this.initFields()
    this.selectedSortParams = [];
  }

}
