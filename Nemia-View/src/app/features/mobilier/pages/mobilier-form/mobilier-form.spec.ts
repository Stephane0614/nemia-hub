import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MobilierForm } from './mobilier-form';

describe('MobilierForm', () => {
  let component: MobilierForm;
  let fixture: ComponentFixture<MobilierForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MobilierForm],
    }).compileComponents();

    fixture = TestBed.createComponent(MobilierForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
