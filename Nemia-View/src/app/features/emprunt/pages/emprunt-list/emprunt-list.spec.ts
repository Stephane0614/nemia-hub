import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmpruntListComponent } from './emprunt-list';

describe('EmpruntListComponent', () => {
  let component: EmpruntListComponent;
  let fixture: ComponentFixture<EmpruntListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmpruntListComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EmpruntListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

