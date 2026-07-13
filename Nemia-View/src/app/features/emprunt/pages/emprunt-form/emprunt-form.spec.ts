import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmpruntFormComponent } from './emprunt-form';

describe('EmpruntFormComponent', () => {
  let component: EmpruntFormComponent;
  let fixture: ComponentFixture<EmpruntFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmpruntFormComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EmpruntFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

