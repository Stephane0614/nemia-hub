import { provideNativeDateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { LOCALE_ID } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExerciceList } from './exercice-list';

describe('ExerciceList', () => {
  let component: ExerciceList;
  let fixture: ComponentFixture<ExerciceList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExerciceList],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        provideNativeDateAdapter(),
        { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' },
        { provide: LOCALE_ID, useValue: 'fr-FR' },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ExerciceList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
