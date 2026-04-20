import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JustificatifForm } from './justificatif-form';

describe('JustificatifForm', () => {
  let component: JustificatifForm;
  let fixture: ComponentFixture<JustificatifForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JustificatifForm],
    }).compileComponents();

    fixture = TestBed.createComponent(JustificatifForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
