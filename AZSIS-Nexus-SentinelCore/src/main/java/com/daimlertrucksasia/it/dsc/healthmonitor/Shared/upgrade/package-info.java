/**
 * Functionality related to upgrading the database.
 *
 * most ORM systems, including hibernate do not handle in-place upgrades very well,
 * so this needs to be handled separately such as the functionality written here.
 *
 * The upgrade system does support an arbitrary of any version to any other version.
 * But this is not utilized quite yet as the negative side effects of batching upgrades need to be evaluated.
 */

package com.daimlertrucksasia.it.dsc.healthmonitor.Shared.upgrade;