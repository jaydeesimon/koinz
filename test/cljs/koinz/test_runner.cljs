(ns koinz.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [koinz.core-test]))

(enable-console-print!)

(doo-tests 'koinz.core-test)


