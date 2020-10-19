import {ComparisonOperation, Field} from "./flat";

export class FilterParam {
  field: Field;
  operation: ComparisonOperation;
  value: string;


  constructor(field: Field, operation: ComparisonOperation, value: string) {
    this.field = field;
    this.operation = operation;
    this.value = value;
  }
}
