import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleAdministrationComponent } from './role-administration.component';

describe('RoleAdministrationComponent', () => {
  let component: RoleAdministrationComponent;
  let fixture: ComponentFixture<RoleAdministrationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RoleAdministrationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RoleAdministrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
