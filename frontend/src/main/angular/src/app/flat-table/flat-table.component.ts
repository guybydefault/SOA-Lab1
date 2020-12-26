import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
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

  // the third one - is the result of comparison
  private compareFlats: Flat[] = [undefined, undefined, undefined];

  constructor(private flatService: FlatService, private toastService: ToastService) {

  }

  ngOnInit(): void {
    this.initCompareFlats();
  }

  deleteFlat(flat: Flat) {
    this.flatService.deleteFlat(flat.id).subscribe(value => {
      this.flatDeleted.emit(flat);
      this.toastService.showSuccess(`Flat with ID #${flat.id} has been successfully deleted.`);
    }, error => {
      this.toastService.showError(`Server returned error (${error.status}: ${error.statusText})`);
    });
  }

  private initCompareFlats() {
    this.compareFlats = [undefined, undefined, undefined];
  }

  isFlatInCompare(flat: Flat): boolean {
    return (flat == this.compareFlats[0] || flat == this.compareFlats[1]) && (this.compareFlats[2] == undefined || flat.id !== this.compareFlats[2].id);
  }

  isFlatWinnerOfComparison(flat: Flat) : boolean {
    return this.compareFlats[2] !== undefined && flat.id == this.compareFlats[2].id;
  }

  compareFlat(flat: Flat) {
    if (this.compareFlats[1] !== undefined || this.compareFlats[2] !== undefined) {
      this.initCompareFlats();
    }

    if (this.compareFlats[0] == undefined) {
      this.compareFlats[0] = flat;
    } else {
      this.compareFlats[1] = flat;
      this.flatService.findCheapest(this.compareFlats[0], this.compareFlats[1]).subscribe(value => {
        this.toastService.showSuccess("Comparison between flats #" + this.compareFlats[0].id + " and #" + this.compareFlats[1].id + " has been made.")
        this.compareFlats[2] = value;
      }, error => {
        this.toastService.showError(`Server returned error (${error.status}: ${error.statusText})`);
      })
    }
  }

  editFlat(flat: Flat) {
    window.scroll(0, 0);
    this.flatForm.fillForm(flat);
  }
}
