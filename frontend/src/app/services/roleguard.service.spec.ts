import { TestBed } from '@angular/core/testing';

import { RoleGuardService } from './roleguard.service';

describe('RoleguardService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RoleGuardService = TestBed.get(RoleGuardService);
    expect(service).toBeTruthy();
  });
});
