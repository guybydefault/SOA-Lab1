import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Flat} from "../domain/flat";
import {FlatService} from "../service/flat.service";
import {FlatFormComponent} from "../flat-form/flat-form.component";
import {ToastService} from "../service/toast.service";

@Component({
  selector: 'app-flat-table',
  templateUrl: './flat-table.component.html',
  styleUrls: ['./flat-table.component.css']
})
export class FlatTableComponent implements OnInit {

  @Output() public flatDeleted: EventEmitter<Flat> = new EventEmitter<Flat>();

  @Input() flats: Flat[]
  @Input() flatForm: FlatFormComponent

  constructor(private flatService: FlatService,private toastService: ToastService) {
  }

  ngOnInit(): void {
  }

  deleteFlat(flat: Flat) {
    this.flatService.deleteFlat(flat.id).subscribe(value => {
      this.flatDeleted.emit(flat);
      this.toastService.showSuccess(`Flat with ID #${flat.id} has been successfully deleted.`);
    }, error => {
      this.toastService.showError(`Server returned error (${error.status}: ${error.statusText})`);
    });
  }

  editFlat(flat: Flat) {
    window.scroll(0,0);
    this.flatForm.fillForm(flat);
  }
}
