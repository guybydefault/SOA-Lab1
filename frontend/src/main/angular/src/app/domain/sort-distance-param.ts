import {Field} from "./flat";
import {Order} from "./sort-param";

export class SortDistanceParam {

  public wayOfTransport: WayOfTransport;
  public order: Order;

  constructor(wayOfTransport: WayOfTransport, order: Order) {
    this.wayOfTransport = wayOfTransport;
    this.order = order;

  }
}

export enum WayOfTransport {
  foot = "foot",
  transport = "transport"
}
