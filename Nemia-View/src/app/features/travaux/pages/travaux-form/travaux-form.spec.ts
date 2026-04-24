import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TravauxForm } from './travaux-form';

describe('TravauxForm', () => {
  let component: TravauxForm;
  let fixture: ComponentFixture<TravauxForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TravauxForm],
    }).compileComponents();

    fixture = TestBed.createComponent(TravauxForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
