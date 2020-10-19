import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FilterParam} from "../domain/filter-param";
import {ComparisonOperation, comparisonOperations, Field, FLAT_FIELDS} from "../domain/flat";

@Component({
  selector: 'app-filter-settings',
  templateUrl: './filter-settings.component.html',
  styleUrls: ['./filter-settings.component.css']
})
export class FilterSettingsComponent implements OnInit {

  @Output() public filterUpdated = new EventEmitter<FilterParam[]>();

  fields: Field[]
  comparisonOperations: ComparisonOperation[]

  selectedOperation: ComparisonOperation;
  selectedField: Field;
  fieldValue: string

  filters: FilterParam[] = []

  constructor() {
    this.comparisonOperations = comparisonOperations
  }

  ngOnInit(): void {
    this.initFields()
  }

  initFields() {
    this.fields = Object.assign([], FLAT_FIELDS);
  }

  addFilter() {
    if (this.validate()) {
      this.filters.push(new FilterParam(this.selectedField, this.selectedOperation, this.fieldValue))
      let ind = this.fields.indexOf(this.selectedField)
      this.fields.splice(ind, 1)
      this.filterUpdated.emit(this.filters)
    }
  }

  validate(): boolean {
    //TODO
    return true;
  }

  clear() {
    this.initFields()
    this.filters = []
  }
}
