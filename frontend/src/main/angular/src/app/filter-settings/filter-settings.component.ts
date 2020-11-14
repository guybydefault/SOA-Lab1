import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FilterParam} from "../domain/filter-param";
import {ComparisonOperation, comparisonOperations, Field, FLAT_FIELDS} from "../domain/flat";
import {ToastService} from "../service/toast.service";

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

  constructor(private toastService: ToastService) {
    this.comparisonOperations = comparisonOperations
  }

  ngOnInit(): void {
    this.initFields()
  }

  initFields() {
    this.fields = Object.assign([], FLAT_FIELDS);
    this.fields = this.fields.filter((field, index) => {
      return field.property != "creationDate";
    });
  }

  addFilter() {
    if (this.validate()) {
      this.filters.push(new FilterParam(this.selectedField, this.selectedOperation, this.fieldValue))
      let ind = this.fields.indexOf(this.selectedField)
      this.fields.splice(ind, 1)
      this.selectedField = undefined;
      this.filterUpdated.emit(this.filters)
    }
  }

  validate(): boolean {
    if (this.selectedField === undefined || this.selectedOperation === undefined || !/\S/.test(this.fieldValue)) {
      this.toastService.showError("Filter validation failed");
      return false;
    }
    return true;
  }

  clear() {
    this.initFields()
    this.filters = []
    this.filterUpdated.emit(this.filters)
  }
}
