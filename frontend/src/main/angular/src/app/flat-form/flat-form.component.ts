import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Coordinates, Flat, FURNISH_TYPES, House, TRANSPORT_TYPES, VIEW_TYPES} from "../domain/flat";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {FlatService} from "../service/flat.service";
import {ToastService} from "../service/toast.service";

@Component({
  selector: 'app-flat-form',
  templateUrl: './flat-form.component.html',
  styleUrls: ['./flat-form.component.css']
})
export class FlatFormComponent implements OnInit {

  @Output() flatEdited: EventEmitter<Flat> = new EventEmitter<Flat>();

  viewTypes = VIEW_TYPES;
  furnishTypes = FURNISH_TYPES;
  transportTypes = TRANSPORT_TYPES;

  flatId: number;

  formGroup: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private flatService: FlatService,
    private toastService: ToastService
  ) {
  }

  ngOnInit(): void {
    this.fillForm(FlatFormComponent.defaultFlat());
  }

  submitEdit() {
    this.submit(this.flatId);
  }

  submitNew() {
    this.flatId = 0;
    this.submit(0);
  }

  private submit(flatId) {
    const {
      name,
      area,
      numberOfRooms,
      furnish,
      view,
      transport,
      x,
      y,
      houseName,
      houseYear,
      houseNumberOfFloors,
      houseFlatsOnFloor,
      houseLifts
    } = this.formGroup.value;

    let flat: Flat = {
      id: flatId,
      name: name,
      area: area,
      numberOfRooms: numberOfRooms,
      furnish: furnish,
      view: view,
      transport: transport,
      coordinates: {
        x: x,
        y: y
      } as Coordinates,
      house: {
        name: houseName,
        year: houseYear,
        numberOfFloors: houseNumberOfFloors,
        numberOfFlatsOnFloor: houseFlatsOnFloor,
        numberOfLifts: houseLifts
      } as House
    } as Flat;

    this.flatService.saveFlat(flat).subscribe(value => {
      this.flatEdited.emit(value);
      this.toastService.showSuccess("Flat has been successfully pushed to server.")
    }, error => {
      this.toastService.showError(`Server returned error (${error.status}: ${error.statusText})`);
    });
  }

  fillForm(flat: Flat) {
    const {name, area, numberOfRooms, furnish, view, transport, coordinates: {x, y}, house: {year, numberOfFloors, numberOfFlatsOnFloor, numberOfLifts}} = flat;
    let houseName = flat.house.name;

    if (!this.formGroup) {
      this.formGroup = this.formBuilder.group(
        {
          name: [name, [Validators.required, Validators.pattern(/\S+/)]],
          area: [area, [Validators.required, Validators.min(0)]],
          numberOfRooms: [numberOfRooms, [Validators.required, Validators.min(0), Validators.max(8)]],
          furnish: [furnish, [Validators.required, Validators.pattern(/\S+/)]],
          view: [view, [Validators.required, Validators.pattern(/\S+/)]],
          transport: [transport, [Validators.required, Validators.pattern(/\S+/)]],
          x: [x, [Validators.required]],
          y: [y, [Validators.required]],
          houseName: [houseName, [Validators.required, Validators.pattern(/\S+/)]],
          houseYear: [year, [Validators.required, Validators.min(0)]],
          houseNumberOfFloors: [numberOfFloors, [Validators.required, Validators.min(0)]],
          houseFlatsOnFloor: [numberOfFlatsOnFloor, [Validators.required, Validators.min(0)]],
          houseLifts: [numberOfLifts, [Validators.required, Validators.min(0)]]
        }
      )
    } else {
      this.formGroup.patchValue({
        name: name,
        area: area,
        numberOfRooms: numberOfRooms,
        furnish: furnish,
        view: view,
        transport: transport,
        x: x,
        y: y,
        houseName: houseName,
        houseYear: year,
        houseNumberOfFloors: numberOfFloors,
        houseFlatsOnFloor: numberOfFlatsOnFloor,
        houseLifts: numberOfLifts
      });
    }
    this.flatId = flat.id;
  }

  public static defaultFlat() {
    return {
      id: 0,
      name: undefined,
      area: undefined,
      numberOfRooms: undefined,
      furnish: FURNISH_TYPES[0],
      view: VIEW_TYPES[0],
      transport: TRANSPORT_TYPES[0],
      coordinates: {
        x: undefined,
        y: undefined,
      },
      house: {
        name: undefined,
        year: undefined,
        numberOfFloors: undefined,
        numberOfFlatsOnFloor: undefined,
        numberOfLifts: undefined
      },
    } as Flat;
  }
}
