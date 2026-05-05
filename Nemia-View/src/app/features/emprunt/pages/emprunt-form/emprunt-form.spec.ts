import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmpruntForm } from './emprunt-form';

describe('EmpruntForm', () => {
  let component: EmpruntForm;
  let fixture: ComponentFixture<EmpruntForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmpruntForm],
    }).compileComponents();

    fixture = TestBed.createComponent(EmpruntForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
