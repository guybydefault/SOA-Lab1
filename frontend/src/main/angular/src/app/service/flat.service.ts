import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Flat} from "../domain/flat";
import {Pageable} from "../domain/pageable";
import {SortParam} from "../domain/sort-param";
import {FilterParam} from "../domain/filter-param";


const baseUrl = 'http://localhost:10241/Lab1_Server/api/flats';

@Injectable({
  providedIn: 'root'
})
export class FlatService {


  constructor(private http: HttpClient) {
  }

  findFlats(pageRequest, sortParams: SortParam[], filterParams: FilterParam[]) {
    console.log(sortParams)
    let params = new HttpParams();
    //TODO encode URI or not?
    for (let sortParam of sortParams) {
      params = params.append('sort', sortParam.field.property + ',' + sortParam.order);
    }
    for (let filterParam of filterParams) {
      params = params.append('filter', filterParam.field.property + ',' + filterParam.operation.operation + ',' + filterParam.value)
    }

    console.log(params)
    return this.http.get<Pageable<Flat>>(`${baseUrl}`, {params});
  }

  deleteFlat() {
    // this.http.delete()
  }

  updateFlat() {
    // this.http.put()
  }

  saveFlat() {
    // this.http.post()
  }

}
