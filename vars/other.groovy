/**
* Building an execution map of pipeline stages.
* By default, all the stages are marked as 'true', executing the whole flow.
* By providing a name of specific stage, all previous stages will be skipped(marked as 'false').
* In case we want to execute only single stage, the one will be marked as 'true', and all the rest as 'false'.
*
* @param start_stage String specifying the name of specific stage.
* @param only_stage Boolean indicate whether we want to execute a single stage.
* @return Map map of pipeline stages. Key=<stage_name>, Value=<Boolean>(true=execute, false=skip)
*/
def get_v2v_current_stage(start_stage, only_stage) {
  def stages = [
                'Create VMs':true,
                'Install Nmon':true,
                'Add extra providers':true,
                'Set RHV provider concurrent VM migration max':true,
                'Conversion hosts enable':true,
                'Configure oVirt conversion hosts':true,
                'Configure ESX hosts':true,
                'Create transformation mappings':true,
                'Create transformation plans':true,
                'Start performance monitoring':true,
                'Execute transformation plans':true,
                'Monitor transformation plans':true,
                'Stop performance monitoring':true
                ]

  for (stage in stages) {
    if (stage.key == start_stage ) {
      if (only_stage)
        continue
      else
        break
    }
    stages[stage.key] = false
  }
  return stages
}

def get_locked_resources() {
  return org.jenkins.plugins.lockableresources.LockableResourcesManager.class.get().getResourcesFromBuild(currentBuild.getRawBuild())
}

def get_locked_ge_name() {
  return get_locked_resources()[0].getName()
}

def get_locked_ge_labels() {
  return get_locked_resources()[0].getLabels()
}

def get_resources_with_label(label){
  return org.jenkins.plugins.lockableresources.LockableResourcesManager.class.get().getResourcesWithLabel(label, null)
}

def get_resource_labels(label){
  return get_resources_with_label(label)[0].getLabels()
}

def is_he_env(env_name) {
  return get_resource_labels(env_name).contains("he-env")
}

def get_username() {
  def cause = currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause)
  if(!cause) // wasn't triggered by user directly
    return "";
  else
    return cause.userId;
}
