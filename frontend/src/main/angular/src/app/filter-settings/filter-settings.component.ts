import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FilterParam} from "../domain/filter-param";
import {ComparisonOperation, comparisonOperations, Field, fields} from "../domain/flat";

@Component({
  selector: 'app-filter-settings',
  templateUrl: './filter-settings.component.html',
  styleUrls: ['./filter-settings.component.css']
})
export class FilterSettingsComponent implements OnInit {

  @Output() filterUpdated = new EventEmitter<FilterParam[]>();

  fields: Field[]
  comparisonOperations: ComparisonOperation[]

  constructor() {
    this.fields = fields
    this.comparisonOperations = comparisonOperations
  }

  ngOnInit(): void {
  }

}
