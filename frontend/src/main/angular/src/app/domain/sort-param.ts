import {Field} from "./flat";

export class SortParam {

  public field: Field;
  public order: Order;

  constructor(field: Field, order: Order) {
    this.field = field;
    this.order = order;
  }
}

export enum Order {
  ASC = "ASC",
  DESC = "DESC"
}
