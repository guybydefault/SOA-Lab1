import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {SortDistanceParam, WayOfTransport} from "../domain/sort-distance-param";
import {Order} from "../domain/sort-param";
import {ToastService} from "../service/toast.service";

@Component({
  selector: 'app-distance-order',
  templateUrl: './distance-order.component.html',
  styleUrls: ['./distance-order.component.css']
})
export class DistanceOrderComponent implements OnInit {

  @Output() distanceOrderUpdated = new EventEmitter<SortDistanceParam>();
  Order = Order;

  fields = [];
  selectedWayOfTransport = undefined;

  constructor(private toastService: ToastService) {
    this.fields = [WayOfTransport.foot.toString(), WayOfTransport.transport.toString()]
  }

  ngOnInit(): void {
  }

  validate(): boolean {
    if (this.selectedWayOfTransport !== undefined) {
      return true;
    } else {
      this.toastService.showError('Way of transport has not been selected.')
      return false;
    }
  }

  setDistanceOrderParam(order: Order) {
    if (this.validate()) {
      this.distanceOrderUpdated.emit(new SortDistanceParam(this.selectedWayOfTransport, order));
    }
  }

  clear() {
    this.distanceOrderUpdated.emit(undefined);
  }


}
