import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BienDetail } from './bien-detail';

describe('BienDetail', () => {
  let component: BienDetail;
  let fixture: ComponentFixture<BienDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BienDetail],
    }).compileComponents();

    fixture = TestBed.createComponent(BienDetail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
