import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmpruntList } from './emprunt-list';

describe('EmpruntList', () => {
  let component: EmpruntList;
  let fixture: ComponentFixture<EmpruntList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmpruntList],
    }).compileComponents();

    fixture = TestBed.createComponent(EmpruntList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
