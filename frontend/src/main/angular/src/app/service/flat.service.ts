import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Flat} from "../domain/flat";
import {PageableDto} from "../domain/pageInfo";
import {Order, SortParam} from "../domain/sort-param";
import {FilterParam} from "../domain/filter-param";
import {PageRequest} from "../domain/page-request";


const baseUrl = 'http://localhost:10250/api/flats';
const agencyUrl = 'http://localhost:10250/s2'

@Injectable({
  providedIn: 'root'
})
export class FlatService {


  constructor(private http: HttpClient) {
  }

  findFlats(pageRequest: PageRequest, sortParams: SortParam[], filterParams: FilterParam[]) {
    let params = new HttpParams();
    //TODO encode URI

    let sortParamsStr = ""
    for (let sortParam of sortParams) {
      if (sortParamsStr !== "") {
        sortParamsStr = sortParamsStr + ";"
      }
      sortParamsStr = sortParamsStr + sortParam.field.property + ',' + sortParam.order;
    }
    params = params.append('sort', sortParamsStr);

    let filterParamsStr = ""
    for (let filterParam of filterParams) {
      if (filterParamsStr !== "") {
        filterParamsStr = filterParamsStr + ";"
      }
      filterParamsStr = filterParamsStr + filterParam.field.property + ',' + filterParam.operation.operation + ',' + filterParam.value;
    }

    params = params.append('filter', filterParamsStr);

    params = params.append('page', pageRequest.pageIndex.toString())
    params = params.append('size', pageRequest.size.toString())

    return this.http.get<PageableDto<Flat>>(`${baseUrl}`, {params});
  }

  findCheapest(f1 : Flat, f2 : Flat) {
      let url = agencyUrl + '/get-cheapest/' + f1.id + '/' + f2.id;
      return this.http.get<Flat>(`${url}`);
  }

  getOrderedByTimeToMetro(type: string, sortOrder: Order) {
    let url = agencyUrl + '/get-ordered-by-time-to-metro/' + type + '/' + sortOrder.toString();
    return this.http.get<Flat[]>(`${url}`);
  }

  deleteFlat(id: number) {
    return this.http.delete<number>(`${baseUrl}/${encodeURIComponent(id)}`);
  }

  saveFlat(flat: Flat) {
    if (flat.id === 0) {
      return this.http.post<Flat>(`${baseUrl}`, flat);
    } else {
      return this.http.put<Flat>(`${baseUrl}/${encodeURIComponent(flat.id)}`, flat);
    }
  }

}
