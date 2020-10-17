import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SortSettingsComponent } from './sort-settings.component';

describe('SortSettingsComponent', () => {
  let component: SortSettingsComponent;
  let fixture: ComponentFixture<SortSettingsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SortSettingsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SortSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
