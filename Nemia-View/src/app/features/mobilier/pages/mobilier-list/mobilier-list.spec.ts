import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MobilierList } from './mobilier-list';

describe('MobilierList', () => {
  let component: MobilierList;
  let fixture: ComponentFixture<MobilierList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MobilierList],
    }).compileComponents();

    fixture = TestBed.createComponent(MobilierList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
