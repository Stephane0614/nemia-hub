import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciceSynthese } from './exercice-synthese';

describe('ExerciceSynthese', () => {
  let component: ExerciceSynthese;
  let fixture: ComponentFixture<ExerciceSynthese>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciceSynthese],
    }).compileComponents();

    fixture = TestBed.createComponent(ExerciceSynthese);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
